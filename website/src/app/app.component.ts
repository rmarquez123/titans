import { Component, OnInit } from '@angular/core';
import {SessionService} from 'src/services/SessionService';
import {ProjectService} from 'src/services/ProjectService';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  public title = 'website 01';
  
  /**
   * 
   */
  public constructor(private session:SessionService, private projectService:ProjectService) {
  }
  
  /**
   * 
   */
  public ngOnInit(): void {
  }
  
}
 