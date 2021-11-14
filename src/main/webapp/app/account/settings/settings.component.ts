import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { LANGUAGES } from 'app/config/language.constants';
import { Router } from '@angular/router';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-settings',
  templateUrl: './settings.component.html',
})
export class SettingsComponent implements OnInit {
  account?: Account;
  success = false;
  languages = LANGUAGES;
  pPicture = '../content/images/default-user-avatar.jpg';
  picture?: string | null | undefined;
  profilePicture?: string | null | undefined;
  counter = 0;

  settingsForm = this.fb.group({
    firstName: [undefined, [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
    lastName: [undefined, [Validators.minLength(1), Validators.maxLength(50)]],
    phone: [undefined, [Validators.required, Validators.minLength(9), Validators.maxLength(13)]],
    email: [undefined, [Validators.minLength(5), Validators.maxLength(254), Validators.email]],
    country: [undefined],
    langKey: [undefined],
  });

  constructor(
    private accountService: AccountService,
    private userService: UserService,
    private fb: FormBuilder,
    private translateService: TranslateService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => {
      if (account) {
        //  this.picture = account.picture;
        //  this.profilePicture = account.profilePicture;
        this.settingsForm.patchValue({
          firstName: account.firstName,
          lastName: account.lastName,
          email: account.email,
          phone: account.login,
          country: account.country,
          langKey: account.langKey,
        });
        // this.setImages(account);
        this.account = account;
      }
    });

    this.setImages(this.account!);
  }

  setImages(account: Account): void {
    this.userService.getUser(this.account!.login).subscribe(user => {
      this.profilePicture = user.profilePicture!;
      this.picture = user.picture;
    });
  }

  save(): void {
    this.success = false;

    this.account!.firstName = this.settingsForm.get('firstName')!.value;
    this.account!.lastName = this.settingsForm.get('lastName')!.value;
    this.account!.email = this.settingsForm.get('email')!.value;
    this.account!.phone = this.settingsForm.get('phone')!.value;
    this.account!.country = this.settingsForm.get('country')!.value;
    this.account!.langKey = this.settingsForm.get('langKey')!.value;

    this.accountService.save(this.account!).subscribe(() => {
      this.success = true;

      this.accountService.authenticate(this.account!);

      if (this.account!.langKey !== this.translateService.currentLang) {
        this.translateService.use(this.account!.langKey);
      }
    });
    this.router.navigate(['']);
  }

  reloadComponent(): void {
    const currentUrl = this.router.url;
    this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.router.onSameUrlNavigation = 'reload';
    this.router.navigate([currentUrl]);
  }
}
