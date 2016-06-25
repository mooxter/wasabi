package com.intuit.wasabi.auditlogobjects;

import com.intuit.wasabi.authenticationobjects.UserInfo;
import com.intuit.wasabi.eventlog.events.*;
import com.intuit.wasabi.experimentobjects.Application;
import com.intuit.wasabi.experimentobjects.Bucket;
import com.intuit.wasabi.experimentobjects.Experiment;

import java.util.Calendar;

/**
 * Provides methods to create AuditLogEntries from EventLogEvents.
 */
public class AuditLogEntryFactory {


    AuditLogEntryFactory() {
    }

    /**
     * Decides which kind of event is supplied and calls the corresponding factory method.
     *
     * @param event the event
     * @return the corresponding {@link AuditLogEntry}.
     */
    public static AuditLogEntry createFromEvent(EventLogEvent event) {
        // time and user are always there
        Calendar time = event.getTime();
        UserInfo user = event.getUser();

        Application.Name appName = null;
        if (event instanceof ApplicationEvent) {
            appName = ((ApplicationEvent) event).getApplicationName();
        }

        Experiment.Label expLabel = null;
        Experiment.ID expId = null;
        if (event instanceof ExperimentEvent) {
            expLabel = ((ExperimentEvent) event).getExperiment().getLabel();
            expId = ((ExperimentEvent) event).getExperiment().getID();
        }

        Bucket.Label bucketLabel = null;
        if (event instanceof BucketEvent) {
            bucketLabel = ((BucketEvent) event).getBucket().getLabel();
        }

        // change events allow for property changes
        String property = null;
        String before = null;
        String after = null;
        if (event instanceof ChangeEvent) {
            property = ((ChangeEvent) event).getPropertyName();
            before = ((ChangeEvent) event).getBefore();
            after = ((ChangeEvent) event).getAfter();
        } else if (event instanceof BucketCreateEvent) {
            property = "allocation";
            after = String.valueOf(((BucketCreateEvent) event).getBucket().getAllocationPercent());
        }

        return new AuditLogEntry(time, user, AuditLogAction.getActionForEvent(event), appName, expLabel, expId, bucketLabel, property, before, after);
    }

}