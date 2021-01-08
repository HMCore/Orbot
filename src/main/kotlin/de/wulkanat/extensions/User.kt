package de.wulkanat.extensions

import com.gitlab.kordlib.core.entity.User
import de.wulkanat.files.Config

val User.isBotAdmin: Boolean
    get() = id.longValue == Config.adminId
