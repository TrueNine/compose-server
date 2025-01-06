package net.yan100.compose.client

typealias CodeRender<T> = CodeBuildable<T>.(renderTarget: T) -> Unit
