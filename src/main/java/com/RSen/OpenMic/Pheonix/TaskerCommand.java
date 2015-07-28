package com.RSen.OpenMic.Pheonix;

import java.io.Serializable;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         TaskerCommand.java
 * @version 1.0
 *          5/28/14
 */
public class TaskerCommand implements Serializable {
    /**
     * Serialization id used for ensuring class has not changed post-serialization
     */
    private static final long serialVersionUID = 6857044522819206055L;
    /**
     * The command that activates this command
     */
    public String activationName;
    /**
     * The name of the Tasker command to activate
     */
    public String taskerCommandName;

    /**
     * Default constructor
     *
     * @param activationName    The command phrase that activates the command
     * @param taskerCommandName The Tasker command name.
     */
    public TaskerCommand(String activationName, String taskerCommandName) {
        this.activationName = activationName;
        this.taskerCommandName = taskerCommandName;
    }
}
