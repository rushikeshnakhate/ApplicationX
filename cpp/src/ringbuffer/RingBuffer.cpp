#include "RingBuffer.h"
#include <thread>
#include <vector>
#include <iostream>
#include <chrono>

// Producer class
class Producer {
public:
    Producer(LockFreeRingBuffer<int, 1024>& buffer, int id)
        : buffer_(buffer), id_(id), running_(true) {}

    void run() {
        int value = 0;
        while (running_) {
            std::string data = "Data-" + std::to_string(value);
            if (buffer_.push({value, data})) {
                std::cout << "Producer " << id_ << " produced: " << value << std::endl;
                value++;
            }
            std::this_thread::sleep_for(std::chrono::milliseconds(100));
        }
    }

    void stop() { running_ = false; }

private:
    LockFreeRingBuffer<int, 1024>& buffer_;
    int id_;
    bool running_;
};

// Consumer class
class Consumer {
public:
    Consumer(LockFreeRingBuffer<int, 1024>& buffer, int id)
        : buffer_(buffer), id_(id), running_(true) {}

    void run() {
        while (running_) {
            typename LockFreeRingBuffer<int, 1024>::Event event;
            if (buffer_.pop(event)) {
                std::cout << "Consumer " << id_ << " consumed: " 
                         << event.value << " - " << event.data << std::endl;
            }
            std::this_thread::sleep_for(std::chrono::milliseconds(10));
        }
    }

    void stop() { running_ = false; }

private:
    LockFreeRingBuffer<int, 1024>& buffer_;
    int id_;
    bool running_;
}; 