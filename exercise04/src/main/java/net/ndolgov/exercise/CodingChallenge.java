package net.ndolgov.exercise;

public interface CodingChallenge {
	void enqueue(String msg) throws QueueException;
	String next(int queue_number) throws QueueException;
}
