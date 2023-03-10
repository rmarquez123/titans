import {Component, ViewEncapsulation, OnInit} from '@angular/core';
import {RastersService} from 'src/services/rasterservices/RastersService';
import {PropertyDetailsViewFactory} from './detailsview/PropertyDetailsViewFactory';


@Component({
  selector: 'properties'
  , templateUrl: './properties.component.html'
  , styleUrls: ['../../../../app/app.component.css', 'properties.component.css']
  , encapsulation: ViewEncapsulation.None
})
export class PropertiesComponent implements OnInit {

  public title: string = "Regions/Points of Interests";

  /**
   * 
   */
  public constructor(private service: RastersService, private factory:PropertyDetailsViewFactory) {
  }

  /**
   * 
   */
  public ngOnInit(): void {
    this.service.getSelectedItem().subscribe(this.onSelectedItem.bind(this));
  }
  
  /**
   * 
   */
  private onSelectedItem(a: any): void {
    this.setDetailsView(a);
  }

  /**
   * 
   */
  private setDetailsView(a: any): void {
    const detailsView = this.factory.get(a);
    this.title = detailsView.getTitle();
    const element = detailsView.getElement();
    $("#propertiesContainer").empty().append(element);
  }
}