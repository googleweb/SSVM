package dev.xdark.ssvm.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Maps VM file descriptors to native descriptors.
 *
 * @author xDark
 */
public interface FileDescriptorManager {

	int READ = 0;
	int WRITE = 1;
	int APPEND = 2;

	/**
	 * Maps VM file descriptor to {@link InputStream}.
	 *
	 * @param handle
	 * 		File descriptor to map.
	 *
	 * @return mapped stream.
	 */
	InputStream getFdIn(long handle);

	/**
	 * Maps VM file descriptor to {@link OutputStream}.
	 *
	 * @param handle
	 * 		File descriptor to map.
	 *
	 * @return mapped stream.
	 */
	OutputStream getFdOut(long handle);

	/**
	 * Called when VM closes file descriptor.
	 *
	 * @param handle
	 * 		VM descriptor handle.
	 */
	void close(long handle);

	/**
	 * Creates new VM file descriptor handle.
	 *
	 * @return file descriptor handle.
	 */
	long newFD();

	/**
	 * Returns new VM file descriptor for standard stream.
	 *
	 * @param stream
	 * 		Standard stream id.
	 *
	 * @return file descriptor handle.
	 */
	long newFD(int stream);

	/**
	 * Returns whether standard stream was opened for appending.
	 *
	 * @param stream
	 * 		Standard stream id.
	 *
	 * @return {@code true} if standard stream was opened for appending,
	 * {@code false} otherwise.
	 */
	boolean isAppend(int stream);

	/**
	 * Canonicalizes file path.
	 *
	 * @param path
	 * 		Path to canonicalize.
	 *
	 * @return canonicalized path.
	 */
	String canonicalize(String path);

	/**
	 * Opens file with the specific mode.
	 *
	 * @param path
	 * 		Path to open
	 * @param mode
	 * 		Open mode.
	 *
	 * @return file handle.
	 *
	 * @throws IOException
	 * 		If any I/O error occurs.
	 * @see FileDescriptorManager#READ
	 * @see FileDescriptorManager#WRITE
	 * @see FileDescriptorManager#APPEND
	 */
	long open(String path, int mode) throws IOException;
}
