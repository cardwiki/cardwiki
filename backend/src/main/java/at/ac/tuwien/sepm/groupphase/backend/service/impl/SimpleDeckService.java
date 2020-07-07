package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckProgressDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckProgressDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.BadRequestException;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ProgressRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RevisionRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimpleDeckService implements DeckService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DeckRepository deckRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CardRepository cardRepository;
    private final RevisionRepository revisionRepository;
    private final ProgressRepository progressRepository;

    public SimpleDeckService(
        DeckRepository deckRepository,
        UserService userService,
        CategoryService categoryService,
        CardRepository cardRepository,
        RevisionRepository revisionRepository,
        ProgressRepository progressRepository)
    {
        this.deckRepository = deckRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.cardRepository = cardRepository;
        this.revisionRepository = revisionRepository;
        this.progressRepository = progressRepository;
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
    public Page<Deck> searchByName(String name, Pageable pageable) {
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

        if (deckUpdate.getName() != null) {
            deck.setName(deckUpdate.getName());
        }

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
            deck.setCategories(deckUpdate.getCategories());
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

    public Page<Revision> getRevisions(Long id, Pageable pageable) {
        LOGGER.debug("Load {} revisions with offset {} from deck {}", pageable.getPageSize(), pageable.getOffset(), id);
        return revisionRepository.findByCard_Deck_Id(id, pageable);
    }

    @Override
    public DeckProgressDto getProgress(Long deckId) {
        LOGGER.debug("Get Progress for deck with id {}", deckId);
        long userId = userService.loadCurrentUserOrThrow().getId();
        int learning = deckRepository.countProgressStatuses(deckId, userId, Progress.Status.LEARNING);
        int reviewing = deckRepository.countProgressStatuses(deckId, userId, Progress.Status.REVIEWING);

        return new DeckProgressDto(deckRepository.countCards(deckId) - learning - reviewing, learning, reviewing);
    }

    @Override
    @Transactional
    public Page<DeckProgressDetailsDto> getLearnedDecksWithStatus(Pageable pageable) {
        LOGGER.debug("Get learned decks");
        long userId = userService.loadCurrentUserOrThrow().getId();
        return deckRepository.findByUserProgress(userId, pageable)
            .map(x -> {
                DeckProgressDetailsDto deckProgressDetailsDto = new DeckProgressDetailsDto();
                deckProgressDetailsDto.setDeckId(x.getId());
                deckProgressDetailsDto.setDeckName(x.getName());
                deckProgressDetailsDto.setLearningCount(deckRepository.countProgressStatuses(x.getId(), userId, Progress.Status.LEARNING));
                deckProgressDetailsDto.setToReviewCount(deckRepository.countProgressStatuses(x.getId(), userId, Progress.Status.REVIEWING));
                deckProgressDetailsDto.setNewCount(deckRepository.countCards(x.getId()) - deckProgressDetailsDto.getLearningCount() - deckProgressDetailsDto.getToReviewCount());
                return deckProgressDetailsDto;
            });
    }

    @Override
    @Transactional
    public void deleteUserProgress(Long deckId) {
        LOGGER.debug("Delete progress for deck with id {}", deckId);
        Long userId = userService.loadCurrentUserOrThrow().getId();
        findOneOrThrow(deckId);
        progressRepository.deleteById_UserIdAndId_Card_Deck_Id(userId, deckId);
    }

    @Override
    @Transactional
    public void createCsvData(PrintWriter pw, Long deckId) throws IOException {
        LOGGER.debug("Write deck with id {} to file.", deckId);
        try (CSVPrinter printer = new CSVPrinter(pw, CSVFormat.DEFAULT)) {
            cardRepository.findLatestEditRevisionsByDeck_Id(deckId)
                .forEach(revisionEdit -> {
                    try {
                        printer.printRecord(revisionEdit.getTextFront(), revisionEdit.getTextBack());
                    } catch (IOException ex) {
                        // Wrapping as unchecked exception to get it outside of lambda expression
                        throw new UncheckedIOException(ex);
                    }
                });
        } catch (UncheckedIOException ex) {
            throw ex.getCause();
        }
    }

    @Override
    @Transactional
    public Deck addCards(Long deckId, MultipartFile file) throws IOException {
        LOGGER.debug("Importing file into deck {}: {}", deckId, file);

        // fetch information
        Deck deck = findOneOrThrow(deckId);
        User user = userService.loadCurrentUserOrThrow();

        // parse csv file
        Reader in = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
        CSVParser csvParser = new CSVParser(in, CSVFormat.DEFAULT.withTrim().withIgnoreSurroundingSpaces());

        // parse into revisions and validate
        List<RevisionCreate> parsedRevisions = csvParser.getRecords().stream()
            .map(csvRecord -> {
                if (csvRecord.size() != 2) {
                    throw new BadRequestException("Every row of the csv file must have exactly 2 columns");
                }
                RevisionCreate revisionCreate = new RevisionCreate();
                revisionCreate.setTextFront(csvRecord.get(0));
                revisionCreate.setTextBack(csvRecord.get(1));

                if (revisionCreate.getTextFront().trim().isEmpty())
                    throw new BadRequestException("Front side may not be empty");
                if (revisionCreate.getTextBack().trim().isEmpty())
                    throw new BadRequestException("Back side may not be empty");
                
                return revisionCreate;
            }).collect(Collectors.toList());
        List<String> parsedFrontTexts = parsedRevisions.stream().map(RevisionEdit::getTextFront).collect(Collectors.toList());

        // get front texts which already exist in deck
        Set<String> existingFrontTexts = cardRepository.filterExistingFrontTexts(deckId, parsedFrontTexts);

        // Add new cards to deck
        List<Card> newCards = parsedRevisions.stream()
            .filter(r -> !existingFrontTexts.contains(r.getTextFront()))
            .map(r -> {
                r.setMessage("Imported");
                r.setCreatedBy(user);
                user.getRevisions().add(r);

                Card card = new Card();
                card.setLatestRevision(r);
                card.getRevisions().add(r);
                r.setCard(card);
                card.setDeck(deck);
                deck.getCards().add(card);

                return card;
            }).collect(Collectors.toList());

        cardRepository.saveAll(newCards);
        return deckRepository.saveAndFlush(deck);
    }
}
