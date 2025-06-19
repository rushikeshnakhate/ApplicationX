FROM gcc:latest

WORKDIR /app
COPY cpp/ .
RUN mkdir build && cd build && cmake .. && make

CMD ["./build/app"]
