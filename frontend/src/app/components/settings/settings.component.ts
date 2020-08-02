import { Component, OnInit } from '@angular/core';
import { TitleService } from 'src/app/services/title.service';
import { ThemeService } from 'src/app/services/theme.service';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss'],
})
export class SettingsComponent implements OnInit {
  public themeConfig: FormControl;

  constructor(private titleService: TitleService, themeService: ThemeService) {
    this.themeConfig = new FormControl(themeService.getConfig());
    this.themeConfig.valueChanges.subscribe((config) =>
      themeService.changeThemeConfig(config)
    );
  }

  ngOnInit(): void {
    this.titleService.setTitle('Settings');
  }
}
