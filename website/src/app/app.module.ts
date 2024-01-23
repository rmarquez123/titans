import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HttpClientModule, HTTP_INTERCEPTORS} from '@angular/common/http';
import {AuthInterceptor} from './auth/AuthInterceptor';
import {HomeComponent} from 'src/pages/Home/HomeComponent';
import {HeaderComponent} from './header/headercomponent';
import {ContactComponent} from 'src/pages/Contact/contactcomponent';
import {SourceComponent} from 'src/pages/Data Sources/source/source.component';
import {DataSourcesComponent} from 'src/pages/Data Sources/datasourcescomponent';

@NgModule({
  declarations: [
    AppComponent, HeaderComponent, HomeComponent, ContactComponent,
    DataSourcesComponent, SourceComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    AppRoutingModule,
    HttpClientModule  
  ],
  providers: [{
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor, multi: true
  }],
  bootstrap: [AppComponent]
})
export class AppModule {}
