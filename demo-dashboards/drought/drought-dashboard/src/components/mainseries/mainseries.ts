

import {Component, OnInit, Input, ViewChild, ElementRef} from '@angular/core';
import * as Highcharts from 'highcharts/highstock';


declare var require: any;
let Boost = require('highcharts/modules/boost');
let noData = require('highcharts/modules/no-data-to-display');
let More = require('highcharts/highcharts-more');

Boost(Highcharts);
noData(Highcharts);
More(Highcharts);
noData(Highcharts);

@Component({
  selector: 'mainseries',
  templateUrl: './mainseries.html',
//  styleUrls: ['']
})
export class MainSeries implements OnInit {
  @Input()
  private useid:string;
  
  private data01 = [];
  private data02 = [];
  
  @ViewChild('viewnode', { static: true }) 
  private mapViewEl!: ElementRef;

  
  public options: any = {
    chart: {
      type: 'area',
      height: "352px" 
    },
    legend : {
      enabled: false
    },
    title: {
      text: null
    },
    credits: {
      enabled: false
    },
    tooltip: {
      formatter: function () {
        let result = 'x: ' + Highcharts.dateFormat('%e %b %y %H:%M:%S', this.x) 
        + 'y: ' + this.y.toFixed(2); 
        return result;
      }
    },
    
    
    xAxis: {
      type: 'datetime',
      labels: {
        formatter: function () {
          return Highcharts.dateFormat('%e %b %y', this.value);
        }
      }
    },
    yAxis : {
      opposite: false
    }, 
    navigator : {
      enabled : false
      
    }
    , scrollbar: false
    , 
    series: [
      {
        name: 'Normal',
        color: "#967E76",
        data: this.data01
      },
      {
        name: 'Abnormal',
        data: this.data02, 
        color: "#9BABB8"
      }
    ]
  }
  public constructor() {
  }


  /**
   * 
   */
  public ngOnInit(): void {
    const starttime = new Date('2018-02-05 18:38:31').getTime();
    const hour = 60*1000*60
    for (let i = 0; i < 50; i++) {
      const t = starttime + i*hour;
      console.log(new Date(t))
      this.data01.push([t, Math.random()]); 
      this.data02.push([t, Math.random()]); 
    }   
    if (this.useid === null || this.useid === undefined) {
      throw new Error("Id is undefined"); 
    }
    Highcharts.stockChart(this.useid, this.options);
  }
    
}

