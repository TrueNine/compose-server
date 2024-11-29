/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.ksp.toolkit

import com.squareup.kotlinpoet.AnnotationSpec

fun AnnotationSpec.Builder.useFile() = useSiteTarget(AnnotationSpec.UseSiteTarget.FILE)

fun AnnotationSpec.Builder.useGet() = useSiteTarget(AnnotationSpec.UseSiteTarget.GET)

fun AnnotationSpec.Builder.useSet() = useSiteTarget(AnnotationSpec.UseSiteTarget.SET)

fun AnnotationSpec.Builder.useField() = useSiteTarget(AnnotationSpec.UseSiteTarget.FIELD)

fun AnnotationSpec.Builder.useDelegate() = useSiteTarget(AnnotationSpec.UseSiteTarget.DELEGATE)

fun AnnotationSpec.Builder.useProperty() = useSiteTarget(AnnotationSpec.UseSiteTarget.PROPERTY)
