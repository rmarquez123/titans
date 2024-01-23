import {Component, OnInit, Input} from '@angular/core';

@Component({
  selector: "sourcesection"
  , templateUrl: './source.component.html'
  , styleUrls: ['./source.component.css']
})
export class SourceComponent implements OnInit {
  @Input()
  public source: string;
  public title: string;
   
  /**
   * 
   */
  public ngOnInit(): void {
    if (this.source === 'nam') {
      this.title = "North American Model from NOAA"; 
    } else if (this.source === 'hrrr') {
      this.title = "High-Resolution Rapid Refresh from NOAA"; 
    } else if (this.source === 'goes') {
      this.title = "Geostationary Operational Environmental Satellite"; 
    }
  }


}
