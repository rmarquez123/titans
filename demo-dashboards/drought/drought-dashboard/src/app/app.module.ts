import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {DroughtHotSpots} from 'src/components/droughhotspots/droughthotspots';
import {MonthlyDryIndex} from 'src/components/monthlydryindex/monthlydryindex';
import {MainSeries} from 'src/components/mainseries/mainseries';
import {AnnualSeries} from 'src/components/annualseries/annualseries';

@NgModule({
  declarations: [
    AppComponent, DroughtHotSpots, MonthlyDryIndex, MainSeries, AnnualSeries
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
