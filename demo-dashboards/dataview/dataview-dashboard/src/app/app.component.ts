import { Component, OnInit } from '@angular/core';
import {SessionService} from 'src/services/SessionService';
import {ProjectService} from 'src/services/ProjectService';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  public title = 'dataview-dashboard';
  public constructor(private session:SessionService, private projectService:ProjectService) {
  }
  public ngOnInit(): void {
    this.session.loggedin.subscribe(loggedin=>{
      if (loggedin){
        this.projectService.setProject(790); 
      }
    }); 
    this.session.logout(); 
    setTimeout(()=>{
      console.log('blah....')
      this.session.login('ricardo.marquez@epstechnologies.com', 'password');
    }, 5000);
  }
  
}
 