const path = require('path');

module.exports = {
    entry: './src/main/resources/static/js/index.js',
    output: {
        path: path.resolve(__dirname, 'src/main/resources/static/dist'),
        filename: 'bundle.js'
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env', '@babel/preset-react']
                    }
                }
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            }
        ]
    },
    resolve: {
        extensions: ['.js', '.jsx']
    },
    devServer: {
        static: {
            directory: path.join(__dirname, 'src/main/resources/static')
        },
        port: 3000,
        proxy: {
            '/api': 'http://localhost:8080'
        }
    }
};
