# Production lightweight web server container for Contact Form
FROM nginx:alpine

# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# Copy webpage assets to nginx document root
COPY webpage/ /usr/share/nginx/html/

# Expose standard HTTP port 80
EXPOSE 80

# Start Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]
