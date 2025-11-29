import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({ selector: 'app-login', templateUrl: './login.page.html' })
export class LoginPage {
  form = this.fb.group({ email: ['', [Validators.required]], password: ['', Validators.required] });
  loading = false; error = '';
  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {}
  async submit(){
    this.loading = true; this.error = '';
    try { await this.auth.login(this.form.value as any); this.router.navigateByUrl('/'); }
    catch(e:any){ this.error = e?.error?.message || 'Erreur'; }
    this.loading = false;
  }
}
