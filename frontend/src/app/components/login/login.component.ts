import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router,ActivatedRoute } from '@angular/router';
import {AuthService} from '../../services/auth.service';
import { OAuthProviders } from 'src/app/dtos/oauthProviders';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  // After first submission attempt, form validation will start
  submitted: boolean = false;
  // Error flag
  error: boolean = false;
  errorMessage: string = '';
  authProviders: OAuthProviders;

  constructor(private formBuilder: FormBuilder, private authService: AuthService, private router: Router, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.authService.getAuthProviders().subscribe(providers => this.authProviders = providers);
    this.route.queryParams.subscribe(params => {
      if ('success' in params){
        this.authService.whoAmI().subscribe(info => {
          // TODO: cache info in localStorage
          if (info.authId === null)
            return;
          if (info.hasAccount) {
            this.router.navigate(['/']);
          } else {
            // TODO: use proper dialog
            let username = prompt('choose your username');
            this.authService.register(info.authId, username).subscribe(status => {
              // TODO: handle errors
              this.router.navigate(['/']);
            });
          }
        });
      }
    });
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

}
