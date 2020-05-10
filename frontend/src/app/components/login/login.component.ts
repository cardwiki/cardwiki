import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router,ActivatedRoute } from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {AuthRequest} from '../../dtos/auth-request';

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

  authProviders = [];

  constructor(private formBuilder: FormBuilder, private authService: AuthService, private router: Router, private route: ActivatedRoute) {
    authService.getAuthProviders().subscribe(providers => this.authProviders = providers);
    this.route.queryParams.subscribe(params => {
      if ('success' in params){
        authService.whoAmI().subscribe(info => {
            console.log('sofar');
          // TODO: cache info in localStorage
          if (info.id != null){
            if (info.hasAccount){
              this.router.navigate(['/']);
            } else {
              // TODO: use proper dialog
              let username = prompt('choose your username');
              authService.register(info.id, username).subscribe(status => {
                // TODO: handle errors
                this.router.navigate(['/']);
              });
            }
          }
        });
      }
    });
  }

  /**
   * Send authentication data to the authService. If the authentication was successfully, the user will be forwarded to the message page
   * @param authRequest authentication data from the user login form
   */
  authenticateUser(authRequest: AuthRequest) {
    console.log('Try to authenticate user: ' + authRequest.email);
    //this.authService.loginUser(authRequest).subscribe(
    //  () => {
    //    console.log('Successfully logged in user: ' + authRequest.email);
    //    this.router.navigate(['/message']);
    //  },
    //  error => {
    //    console.log('Could not log in due to:');
    //    console.log(error);
    //    this.error = true;
    //    if (typeof error.error === 'object') {
    //      this.errorMessage = error.error.error;
    //    } else {
    //      this.errorMessage = error.error;
    //    }
    //  }
    //);
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  ngOnInit() {
  }

}
