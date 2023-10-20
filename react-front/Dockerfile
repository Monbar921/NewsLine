FROM node:lts-alpine AS builder
ADD . application
WORKDIR /application
RUN npm install
RUN npm run build

FROM nginx:stable-alpine
COPY --from=builder /application/dist /usr/share/nginx/html
COPY --from=builder /application/nginx_config/default.conf /etc/nginx/conf.d
CMD ["nginx", "-g", "daemon off;"]
