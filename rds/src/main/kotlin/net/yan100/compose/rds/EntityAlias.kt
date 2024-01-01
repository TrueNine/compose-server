package net.yan100.compose.rds

import jakarta.persistence.*


typealias Col = Column
typealias Fk = ForeignKey
typealias Jc = JoinColumn
typealias Jt = JoinTable

typealias Otm = OneToMany
typealias Mto = ManyToOne
typealias Mtm = ManyToMany
typealias Oto = OneToOne

val NO_C = ConstraintMode.NO_CONSTRAINT
