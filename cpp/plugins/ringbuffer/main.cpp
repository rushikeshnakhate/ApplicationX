#include "RingBuffer.h"
#include <thread>
#include <vector>
#include <iostream>
#include <chrono>

int main() {
    // Create ring buffer
    LockFreeRingBuffer<int, 1024> buffer;

    // Create producers and consumers
    std::vector<Producer> producers;
    std::vector<Consumer> consumers;
    std::vector<std::thread> producer_threads;
    std::vector<std::thread> consumer_threads;

    // Create 2 producers
    for (int i = 0; i < 2; ++i) {
        producers.emplace_back(buffer, i);
    }

    // Create 4 consumers
    for (int i = 0; i < 4; ++i) {
        consumers.emplace_back(buffer, i);
    }

    // Start producers
    for (auto& producer : producers) {
        producer_threads.emplace_back(&Producer::run, &producer);
    }

    // Start consumers
    for (auto& consumer : consumers) {
        consumer_threads.emplace_back(&Consumer::run, &consumer);
    }

    // Let it run for 10 seconds
    std::this_thread::sleep_for(std::chrono::seconds(10));

    // Stop producers and consumers
    for (auto& producer : producers) {
        producer.stop();
    }
    for (auto& consumer : consumers) {
        consumer.stop();
    }

    // Wait for all threads to finish
    for (auto& thread : producer_threads) {
        thread.join();
    }
    for (auto& thread : consumer_threads) {
        thread.join();
    }

    // Print final statistics
    std::cout << "\nRing Buffer Statistics:" << std::endl;
    std::cout << "Size: " << buffer.size() << std::endl;
    std::cout << "Empty: " << (buffer.empty() ? "Yes" : "No") << std::endl;
    std::cout << "Full: " << (buffer.full() ? "Yes" : "No") << std::endl;

    return 0;
} 