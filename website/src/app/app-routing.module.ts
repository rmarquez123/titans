import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {HomeComponent} from 'src/pages/Home/HomeComponent';
import {ContactComponent} from 'src/pages/Contact/contactcomponent';
import {DataSourcesComponent} from 'src/pages/Data Sources/datasourcescomponent';


const routes: Routes = [
  { path: '', component : HomeComponent}
  , { path: 'home', component : HomeComponent}
  , { path: 'contact', component : ContactComponent}
  , { path: 'datasets', component : DataSourcesComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {

}
export const routeComponents = [  
  routes.map(r => r.component)
];

