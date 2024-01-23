import {OnInit, Component} from '@angular/core';
import {HeaderItem} from './headeritem';
import {Router, ActivatedRoute, NavigationEnd} from '@angular/router';
import {filter} from 'rxjs/operators';


@Component({
  selector: "customheader", templateUrl: './headercomponent.html'
  , styleUrls: ["./headercomponent.css"]
})
export class HeaderComponent implements OnInit {
  public title: string = "Titans";
  public items: HeaderItem[] = [
    new HeaderItem({title: "Home", path: "/home", items: []}),
    new HeaderItem({title: "DataSets", path: "/datasets", items: []}),
    new HeaderItem({title: "API Documentation", path: "/apidocumentation", items: []}),
    new HeaderItem({title: "Blogs", path: "/blogs", items: []}),
    new HeaderItem({title: "Dashboards", path: "/dashboards", items: []}),
    new HeaderItem({title: "Register", path: "/register", items: []}),
    new HeaderItem({title: "Contact", path: "/contact", items: []}),
  ];
  currentRoute: string;

  /**
   * 
   */
  public constructor(private router: Router, private activatedRoute: ActivatedRoute) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.currentRoute = event.url;
    });
  }
  /**
   * 
   */
  public ngOnInit(): void {

  }
  
  public isRouteActive(path: string): boolean {
    console.log(this.currentRoute); 
    return this.currentRoute === path || (this.currentRoute === '/' && path === "/home");
  }
}


