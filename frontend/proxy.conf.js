// Why a .js file: the backend address differs per mode.
// Locally it is localhost:8080; inside docker-compose the backend is the "app" container.
module.exports = {
  '/api': {
    target: process.env.API_TARGET || 'http://localhost:8080',
    secure: false,
  },
};
