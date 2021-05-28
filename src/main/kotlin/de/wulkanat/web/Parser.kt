package de.wulkanat.web

import de.wulkanat.extensions.absUrl
import de.wulkanat.extensions.get
import de.wulkanat.extensions.imgSrc
import de.wulkanat.extensions.text
import de.wulkanat.model.BlogPostPreview
import de.wulkanat.model.JobListingPreview

private const val BLOG_POST_STATE_FILE_NAME = "blog_state.json"
fun fakeUpdateBlogPost() = removeFirstFromSiteSave<BlogPostPreview>(BLOG_POST_STATE_FILE_NAME)
fun getNewBlogPosts() = updateSite("https://hytale.com/news", BLOG_POST_STATE_FILE_NAME) { doc ->
    doc["postWrapper"].map {
        BlogPostPreview(
            title = it["post__details__heading"].text,
            imgUrl = it["post__image__frame"].first().imgSrc,
            fullPostUrl = it.absUrl,
            date = it["post__details__meta__date"].text,
            author = it["post__details__meta__author"].text,
            description = it["post__details__body"].text,
        )
    }
}

private const val JOB_LISTING_STATE_FILE_NAME = "jobs_state.json"
fun fakeUpdateJobListings() = removeFirstFromSiteSave<JobListingPreview>(JOB_LISTING_STATE_FILE_NAME)
fun getNewJobListings() = updateSite("https://hypixelstudios.com/jobs/", JOB_LISTING_STATE_FILE_NAME) { doc ->
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
}
