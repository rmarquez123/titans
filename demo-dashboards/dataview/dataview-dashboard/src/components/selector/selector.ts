import {OnInit, Component} from '@angular/core';
import {DataState} from 'src/services/DataState';
import {Source} from 'src/services/Source';
import {BehaviorSubject} from 'rxjs';
import {SourceVariable} from 'src/services/SourceVariable';
import {MapState} from 'src/services/MapState';


@Component({
  selector: 'selector',
  templateUrl: './selector.html',
  styleUrls: ['./selector.css']
})
export class Selector implements OnInit {
  public sources: Source[];
  public vars: SourceVariable[] = [];

  private _selectedSource = new BehaviorSubject<number>(null);
  public selectedSource$ = this._selectedSource.asObservable();

  private _selectedVar = new BehaviorSubject<string>(null);
  public selectedVar$ = this._selectedVar.asObservable();

  public selectedDate: string;



  public constructor(private datastate: DataState, private mapState:MapState) {
  }

  /**
   * 
   */
  public ngOnInit(): void {
    this.datastate.getSources().then(s => {
      this.sources = s;
      this._selectedSource.next(s[0].rasterId);
    });
    this._selectedSource.subscribe(s => {
      this.vars = [];
      if (s != null) {
        this.datastate.getSourceVariables(s).then(p => {
          this.vars = p;
        });
      }
    });
  }

  public set selectedSource(value: number) {
    this._selectedSource.next(value);
  }

  public get selectedSource(): number {
    return this._selectedSource.getValue();
  }

  public set selectedVar(value: string) {
    this._selectedVar.next(value);
  }

  public get selectedVar(): string {
    return this._selectedVar.getValue();
  }

  /**
   * 
   */
  public onSubmit(): void {
    const rasterId: number = this._selectedSource.getValue();
    const variable: string = this._selectedVar.getValue();
    this.datastate.getRasterImage(rasterId, "202310121200", variable).then(image => {
      this.mapState.addMap(image); 
    });
    console.log("submit");
  }
}
