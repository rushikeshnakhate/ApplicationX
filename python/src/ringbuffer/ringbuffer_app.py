import asyncio
import time
from typing import Any, Optional
from dataclasses import dataclass
import logging

@dataclass
class Event:
    value: int
    data: str
    timestamp: float

class AsyncRingBuffer:
    def __init__(self, size: int = 1024):
        self.size = size
        self.queue = asyncio.Queue(maxsize=size)
        self._producer_count = 0
        self._consumer_count = 0

    async def put(self, event: Event) -> None:
        """Put an event into the ring buffer."""
        await self.queue.put(event)
        self._producer_count += 1

    async def get(self) -> Optional[Event]:
        """Get an event from the ring buffer."""
        try:
            event = await self.queue.get()
            self._consumer_count += 1
            return event
        except asyncio.QueueEmpty:
            return None

    def get_stats(self) -> dict:
        """Get statistics about the ring buffer."""
        return {
            'size': self.size,
            'current_size': self.queue.qsize(),
            'producer_count': self._producer_count,
            'consumer_count': self._consumer_count
        }

class Producer:
    def __init__(self, ring_buffer: AsyncRingBuffer, producer_id: int):
        self.ring_buffer = ring_buffer
        self.producer_id = producer_id
        self.running = True

    async def produce(self):
        """Produce events and put them into the ring buffer."""
        value = 0
        while self.running:
            event = Event(
                value=value,
                data=f"Data-{value}",
                timestamp=time.time()
            )
            await self.ring_buffer.put(event)
            value += 1
            await asyncio.sleep(0.1)  # Simulate some work

class Consumer:
    def __init__(self, ring_buffer: AsyncRingBuffer, consumer_id: int):
        self.ring_buffer = ring_buffer
        self.consumer_id = consumer_id
        self.running = True

    async def consume(self):
        """Consume events from the ring buffer."""
        while self.running:
            event = await self.ring_buffer.get()
            if event:
                print(f"Consumer {self.consumer_id} processed: {event}")
            await asyncio.sleep(0.01)  # Prevent busy waiting

async def main():
    # Create ring buffer
    ring_buffer = AsyncRingBuffer(size=1024)

    # Create producers and consumers
    producers = [Producer(ring_buffer, i) for i in range(2)]
    consumers = [Consumer(ring_buffer, i) for i in range(4)]

    # Start producers and consumers
    producer_tasks = [asyncio.create_task(p.produce()) for p in producers]
    consumer_tasks = [asyncio.create_task(c.consume()) for c in consumers]

    # Let it run for a while
    await asyncio.sleep(10)

    # Stop producers and consumers
    for p in producers:
        p.running = False
    for c in consumers:
        c.running = False

    # Wait for tasks to complete
    await asyncio.gather(*producer_tasks, *consumer_tasks)

    # Print statistics
    print("\nRing Buffer Statistics:")
    print(ring_buffer.get_stats())

if __name__ == "__main__":
    asyncio.run(main()) 