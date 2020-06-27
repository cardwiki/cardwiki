import { Component, OnInit } from '@angular/core';
import { TitleService } from 'src/app/services/title.service';

@Component({
  selector: 'app-markdown-syntax',
  templateUrl: './markdown-syntax.component.html',
  styleUrls: ['./markdown-syntax.component.css']
})
export class MarkdownSyntaxComponent implements OnInit {

  public examples: { [key: string]: string } = {
    styles: `Make *italic*, **bold** or ~~strike-through~~ text by wrapping it in *, ** or ~~ respectively.`,
    structuring: `Create lists by starting lines with - or 1.
- first
- second
  - third

1. first
2. second
 
 And tables like this
| First Header  | Second Header | Third Header |
| ------------- | ------------- | ------------ |
| Content Cell  | Content Cell  | Content Cell |
| Content Cell  | Content Cell  | Content Cell |`,
    code: `Add \`inline code\` by wrapping inside \`

\`\`\`
Add code blocks by wrapping inside three \`s
\`\`\``,
    math: `Add inline math expressions by wrapping it inside $: $\\pi \\ge 2*x$. For a math block wrap it inside two $ signs with an empty line before it:

$$x = \\frac{a}{\\sqrt[3]{b}}$$

$$f(x) = \\int_{-\\infty}^\\infty
    \\hat f\\xi\\,e^{2 \\pi i \\xi x}
    \\,d\\xi$$`
  }

  constructor(private titleService: TitleService) { }

  ngOnInit(): void {
    this.titleService.setTitle('Formatting Cards');
  }

}
