import {Component} from '@angular/core';
import {Router} from '@angular/router';


@Component({
  selector : "home"
  , templateUrl: './homecomponent.html'
  , styleUrls: ['./homecomponent.css']
})
export class HomeComponent {
  
  /**
   * 
   */
  public constructor(private router:Router) {
  }
  
  
  public onExploreDatasets():void  {
    this.router.navigate(["/datasets"]); 
  }
}


