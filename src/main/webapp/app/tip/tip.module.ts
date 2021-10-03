import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TipComponent } from './tip.component';
import { RouterModule } from '@angular/router';
import { TIP_ROUTE } from './tip.route';

@NgModule({
  declarations: [TipComponent],
  imports: [CommonModule, RouterModule.forChild([TIP_ROUTE])],
})
export class TipModule {}
