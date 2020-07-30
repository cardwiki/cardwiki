import { Pipe, PipeTransform } from '@angular/core';
import * as MarkdownIt from 'markdown-it';
// TODO: add definition file for markdown-it-texmath
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore: Could not find a declaration file for module 'markdown-it-texmath'
import * as MarkdownItTexmath from 'markdown-it-texmath';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Pipe({
  name: 'md2html',
})
export class Md2htmlPipe implements PipeTransform {
  private converter: MarkdownIt;

  constructor(private sanitizer: DomSanitizer) {
    this.converter = new MarkdownIt({
      breaks: true,
    })
      .disable(['heading', 'lheading'])
      .use(MarkdownItTexmath, {
        engine: 'katex',
        delimiters: 'dollars',
      });
    this.converter.validateLink = () => false;
  }

  transform(markdown: string): SafeHtml {
    const rendered = this.postProcess(this.converter.render(markdown));

    // custom html and links are disabled, hence it should be safe
    // angulars sanitizer strips unknown tags which are used by Katex (like <math>)
    return this.sanitizer.bypassSecurityTrustHtml(rendered);
  }

  private postProcess(html: string): string {
    return html.replace(/<table>/gi, '<table class="table">');
  }
}
