package de.wulkanat.web

import de.wulkanat.extensions.get
import de.wulkanat.extensions.text
import de.wulkanat.extensions.absUrl
import de.wulkanat.model.JobListingPreview
import org.jsoup.nodes.Document

fun parseJobListings(doc: Document) =
    doc["current-jobs__departments"].flatMap { jobDepartment ->
        val jobDepartmentName = jobDepartment["current-jobs__department-name"].text

        jobDepartment["current-jobs__job"].map { job ->
            JobListingPreview(
                title = job["current-jobs__job-title"].text,
                department = jobDepartmentName,
                location = job["current-jobs__job-location"].text,
                fullListingUrl = job.absUrl
            )
        }
    }