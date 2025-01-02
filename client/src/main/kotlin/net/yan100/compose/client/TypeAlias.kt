package net.yan100.compose.client

typealias FileRender<T> = CodeBuildable<T>.(file: T) -> Unit
