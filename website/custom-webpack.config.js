const path = require('path');

module.exports = {
  module: {
    rules: [
      {
        test: /\.(m?js)$/,
        exclude: /node_modules\/(?!(@arcgis\/core)\/).*/,
        use: {
          loader: "babel-loader",
          options: {
            presets: [
              "@babel/preset-env"
            ],
            plugins: [
              ["@babel/plugin-proposal-nullish-coalescing-operator", {loose: true}],
              ["@babel/plugin-proposal-optional-chaining", {loose: true}]
            ]
          }
        }
      }
    ]
  }
};
