
// istanbul ignore file

// https://github.com/chimurai/http-proxy-middleware
const proxy = require('http-proxy-middleware');

const rewriteFn = function (path, req) {
  
  path = path.replace('/oslc/', '/api/');
        path = path.replace('/api/api/', '/api/');
        path = path.includes('?')? path + '&apikey=98ovtvgjevedoso68qhrfgo85u087gsan3acsofg' : path + '?apikey=98ovtvgjevedoso68qhrfgo85u087gsan3acsofg';
  return path;
};
    

module.exports = function(app) {
  app.use(proxy.createProxyMiddleware('/maximo/**', {
      target: 'https://masdemo.manage.maslab.apps.tech.am.co-demo.com:443',
      changeOrigin: true,
      pathRewrite: rewriteFn
      
    }));
};
    