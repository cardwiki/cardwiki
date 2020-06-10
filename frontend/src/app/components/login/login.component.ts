import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Router,ActivatedRoute } from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {parse as parseCookie} from 'cookie';
import { OAuth2ProviderDto } from 'src/app/dtos/oAuth2Provider';
import { WhoAmI } from 'src/app/dtos/whoAmI';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  authProviders: OAuth2ProviderDto[];
  oAuthInfo: WhoAmI;
  registerForm: FormGroup;
  username: string;

  constructor(public authService: AuthService, private router: Router, private route: ActivatedRoute, private notificationService: NotificationService) {
    this.registerForm = new FormGroup({
      'username': new FormControl(this.username, [
        Validators.required,
        Validators.minLength(1),
        Validators.pattern(/^[a-z0-9_-]+$/)
      ]),
    });
  }

  get formUsername() { return this.registerForm.get('username'); }

  ngOnInit(): void {
    this.authService.getAuthProviders().subscribe(providers => this.authProviders = providers);
    this.route.queryParams.subscribe(params => {
      if ('success' in params)
        this.handleSuccessfulLogin()
    });
  }

  private handleSuccessfulLogin(): void {
    this.authService.updateToken(parseCookie(document.cookie).token);
    this.authService.whoAmI().subscribe(info => {
      this.oAuthInfo = info;
      console.log(info);
      if (info.authId === null)
        return; //TODO proper error handling?
      if (info.hasAccount) {
        this.router.navigate(['/']);
      }
    });
  }

  register(username: string) {
    this.authService.register(username).subscribe(status => {
      console.log("Status: ", status);
      if (status.username) {
        this.username = status.username;
        setTimeout(() =>
          {
            this.registerForm.reset();
            this.oAuthInfo = null;
            this.router.navigate(['/']);
          },
          2500);
      }
    });
  }

  _textValue:string;
  ConvertToLower(evt: string) {
    if (evt)
      this._textValue = evt.toLowerCase();
  }

}
