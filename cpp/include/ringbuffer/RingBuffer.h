#pragma once

#include <atomic>
#include <array>
#include <chrono>
#include <string>
#include <memory>
#include <thread>
#include <iostream>

template<typename T, size_t Size>
class LockFreeRingBuffer {
public:
    struct Event {
        T value;
        std::string data;
        std::chrono::system_clock::time_point timestamp;

        Event() = default;
        Event(T val, const std::string& d) 
            : value(val), data(d), timestamp(std::chrono::system_clock::now()) {}
    };

    LockFreeRingBuffer() : head_(0), tail_(0) {}

    bool push(const Event& event) {
        size_t head = head_.load(std::memory_order_relaxed);
        size_t next_head = (head + 1) % Size;

        if (next_head == tail_.load(std::memory_order_acquire)) {
            return false; // Buffer is full
        }

        buffer_[head] = event;
        head_.store(next_head, std::memory_order_release);
        return true;
    }

    bool pop(Event& event) {
        size_t tail = tail_.load(std::memory_order_relaxed);

        if (tail == head_.load(std::memory_order_acquire)) {
            return false; // Buffer is empty
        }

        event = buffer_[tail];
        tail_.store((tail + 1) % Size, std::memory_order_release);
        return true;
    }

    size_t size() const {
        size_t head = head_.load(std::memory_order_acquire);
        size_t tail = tail_.load(std::memory_order_acquire);
        return (head - tail) % Size;
    }

    bool empty() const {
        return head_.load(std::memory_order_acquire) == 
               tail_.load(std::memory_order_acquire);
    }

    bool full() const {
        size_t next_head = (head_.load(std::memory_order_acquire) + 1) % Size;
        return next_head == tail_.load(std::memory_order_acquire);
    }

private:
    std::array<Event, Size> buffer_;
    std::atomic<size_t> head_;
    std::atomic<size_t> tail_;
}; 