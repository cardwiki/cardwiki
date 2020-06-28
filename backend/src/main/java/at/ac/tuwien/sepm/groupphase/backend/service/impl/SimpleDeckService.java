package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MutableCallSite;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SimpleDeckService implements DeckService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DeckRepository deckRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CardRepository cardRepository;

    public SimpleDeckService(DeckRepository deckRepository, UserService userService, CategoryService categoryService,
                             CardRepository cardRepository) {
        this.deckRepository = deckRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.cardRepository = cardRepository;
    }

    @Transactional
    @Override
    public Deck findOneOrThrow(Long id) {
        LOGGER.debug("Find deck with id {}", id);
        Objects.requireNonNull(id, "id argument must not be null");
        Optional<Deck> deck = deckRepository.findById(id);
        return deck.orElseThrow(() -> new DeckNotFoundException(String.format("Could not find card deck with id %s", id)));
    }

    @Override
    public List<Deck> searchByName(String name, Pageable pageable) {
        LOGGER.debug("Search card decks for name {} {}", name, pageable);
        Objects.requireNonNull(name, "name argument must not be null");
        return deckRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Transactional
    @Override
    public Deck create(Deck deck) {
        LOGGER.debug("Create new deck {}", deck);
        Objects.requireNonNull(deck, "deck argument must not be null");
        User user = userService.loadCurrentUserOrThrow();
        deck.setCreatedBy(user);
        deck.setCreatedBy(user);
        return deckRepository.save(deck);
    }

    @Transactional
    @Override
    public Deck update(Long id, Deck deckUpdate) {
        LOGGER.debug("Update deck with id: {}", id);
        Deck deck = findOneOrThrow(id);
        deck.setName(deckUpdate.getName());
        Set<Category> categories = deck.getCategories();

        if (deckUpdate.getCategories() != null) {
            //add deck to new categories
            for (Category category : deckUpdate.getCategories()) {
                category = categoryService.findOneOrThrow(category.getId());
                category.getDecks().add(deck);
                categories.remove(category);
            }
            //remove deck from removed categories
            for (Category category : categories) {
                category = categoryService.findOneOrThrow(category.getId());
                category.getDecks().remove(deck);
            }
        }

        return deckRepository.save(deck);
    }

    @Transactional
    @Override
    public Deck copy(Long id, Deck deckCopy) {
        LOGGER.debug("Copy deck with id: {}", id);
        User currentUser = userService.loadCurrentUserOrThrow();
        Deck source = findOneOrThrow(id);

        Deck deck = create(deckCopy);
        deck.setCategories(new HashSet<>(source.getCategories()));
        for (Category category : source.getCategories()) {
            category.getDecks().add(deck);
        }
        deckRepository.save(deck);
        List<Card> cards = cardRepository.findLatestEditRevisionsByDeck_Id(id).map(sourceRevision -> {
            Card card = new Card();
            card.setDeck(deck);
            RevisionCreate rev = new RevisionCreate();
            rev.setMessage(String.format("Copied from deck %s.", id));
            rev.setCreatedBy(currentUser);
            rev.setCard(card);
            rev.setTextFront(sourceRevision.getTextFront());
            rev.setTextBack(sourceRevision.getTextBack());
            rev.setImageFront(sourceRevision.getImageFront());
            rev.setImageBack(sourceRevision.getImageBack());
            card.setLatestRevision(rev);
            return card;
        }).collect(Collectors.toList());

        cardRepository.saveAll(cards);
        cardRepository.flush();
        deck.getCards().addAll(cards);
        return deck;
    }

    @Override
    public void delete(Long id) {
        LOGGER.debug("Delete deck with id {}", id);
        try {
            deckRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DeckNotFoundException(String.format("Could not find card deck with id %d", id));
        }
    }

    @Override
    @Transactional
    public void createCsvData(PrintWriter pw, Long deckId) throws IOException {
        LOGGER.debug("Write deck with id {} to file.", deckId);
        CSVPrinter printer = new CSVPrinter(pw, CSVFormat.DEFAULT);
        cardRepository.findLatestEditRevisionsByDeck_Id(deckId)
            .forEach(revisionEdit -> {
                try {
                    printer.printRecord(revisionEdit.getTextFront(), revisionEdit.getTextBack());
                } catch (IOException e) {
                    LOGGER.info("An error occurred converting records to csv: {}", e);
                }
            });
        printer.close();
    }

    @Override
    @Transactional
    public Deck addCards(Long deckId, MultipartFile file) throws IOException {
        // fetch information
        Deck deck = deckRepository.getOne(deckId);
        User user = userService.loadCurrentUserOrThrow();
        // parse csv file
        Reader in = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
        CSVParser csvParser = new CSVParser(in, CSVFormat.DEFAULT.withTrim());
        Iterable<CSVRecord> csvRecords = csvParser.getRecords();

        // create new cards
        for (CSVRecord csvRecord : csvRecords) {
            String textFront = csvRecord.get(0);

            if(!cardRepository.existsByDeckAndRevisionEditContent(deckId, textFront)) {
                String textBack = csvRecord.get(1);

                Card card = new Card();
                card.setDeck(deck);
                deck.getCards().add(card);
                RevisionCreate revisionCreate = new RevisionCreate();
                revisionCreate.setMessage("Created");
                revisionCreate.setTextFront(textFront);
                revisionCreate.setTextBack(textBack);
                revisionCreate.setCard(card);
                revisionCreate.setCreatedBy(user);
                user.getRevisions().add(revisionCreate);
                card.setLatestRevision(revisionCreate);

                deckRepository.saveAndFlush(deck);
            } else {
                LOGGER.info("Card with front {} already exists.", csvRecord.get(0));
            }

        }
        Hibernate.initialize(deck.getCategories());
        return deck;
    }
}
