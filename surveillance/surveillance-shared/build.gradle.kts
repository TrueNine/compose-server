plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

dependencies { implementation(projects.shared) }

description =
  "Shared components and utilities for surveillance systems integration. " +
    "Provides common interfaces, data models, and utility functions for various surveillance device protocols."
