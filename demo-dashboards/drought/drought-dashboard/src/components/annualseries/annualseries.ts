

import {Component, OnInit, Input, ViewChild, ElementRef} from '@angular/core';
import * as Highcharts from 'highcharts';

declare var require: any;
let Boost = require('highcharts/modules/boost');
let noData = require('highcharts/modules/no-data-to-display');
let More = require('highcharts/highcharts-more');

Boost(Highcharts);
noData(Highcharts);
More(Highcharts);
noData(Highcharts);

@Component({
  selector: 'annualseries',
  templateUrl: './annualseries.html',
  //  styleUrls: ['']
})
export class AnnualSeries implements OnInit {
  @Input()
  private useid: string;

  @ViewChild('viewnode', {static: true})
  private mapViewEl!: ElementRef;


  public options: any = {
    chart: {
      type: 'column',
      height: "150px",
      style: {
        fontSize: "0.8rem"
      }
      , margin: undefined
      , marginBottom: 6
    },
    legend: {
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
    yAxis: {
      title: {
        enabled: false
      }
    },
    series: [
      {
        name: 'Normal',
        turboThreshold: 500000,
        data: [[new Date('2018-01-25 18:38:31').getTime(), 2]]
        , color: "#967E76"
      },
      {
        name: 'Abnormal',
        turboThreshold: 500000,
        data: [
          [new Date('2018-01-25 18:38:31').getTime(), 7], 
          [new Date('2018-02-05 18:38:31').getTime(), 10], 
          [new Date('2018-02-25 18:38:31').getTime(), 6]
        ]
        , color: "#9BABB8"
      }
    ]
  }
  public constructor() {
  }


  /**
   * 
   */
  public ngOnInit(): void {
    if (this.useid === null || this.useid === undefined) {
      throw new Error("Id is undefined");
    }

    Highcharts.chart(this.useid, this.options);
  }

}

