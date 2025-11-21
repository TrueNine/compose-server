package io.github.truenine.composeserver.data.extract.service

interface IChinaFirstNameService {
  companion object {
    fun matches(firstName: String): Boolean = firstName.isNotBlank() && firstName in CHINA_FIRST_NAMES

    val CHINA_FIRST_NAMES: Array<String>
      get() =
        arrayOf(
          "Smith",
          "Johnson",
          "Williams",
          "Brown",
          "Jones",
          "Garcia",
          "Miller",
          "Davis",
          "Rodriguez",
          "Martinez",
          "Hernandez",
          "Lopez",
          "Gonzalez",
          "Wilson",
          "Anderson",
          "Thomas",
          "Taylor",
          "Moore",
          "Jackson",
          "Martin",
          "Lee",
          "Perez",
          "Thompson",
          "White",
          "Harris",
          "Sanchez",
          "Clark",
          "Ramirez",
          "Lewis",
          "Robinson",
          "Walker",
          "Young",
          "Allen",
          "King",
          "Wright",
          "Scott",
          "Torres",
          "Nguyen",
          "Hill",
          "Flores",
          "Green",
          "Adams",
          "Nelson",
          "Baker",
          "Hall",
          "Rivera",
          "Campbell",
          "Mitchell",
          "Carter",
          "Roberts",
        )
  }
}
