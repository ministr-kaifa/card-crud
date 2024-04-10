FROM node:20
WORKDIR /app
COPY /crud-front/app/package.json .
COPY /crud-front/app/package-lock.json .
RUN npm install
COPY /crud-front/app .
CMD npm start
