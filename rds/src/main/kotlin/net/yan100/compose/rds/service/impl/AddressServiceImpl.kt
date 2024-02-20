package net.yan100.compose.rds.service.impl

import jakarta.persistence.criteria.CriteriaQuery
import net.yan100.compose.rds.core.entities.AnyEntity
import net.yan100.compose.rds.core.entities.withNew
import net.yan100.compose.rds.entities.Address
import net.yan100.compose.rds.repositories.address.IAddressRepo
import net.yan100.compose.rds.service.IAddressService
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.stereotype.Service

@Service
class AddressServiceImpl(
    private val repo: IAddressRepo
) : IAddressService, CrudService<Address>(repo) {
    override fun findRoot(): Address {
        return repo.findRoot()
    }

    override fun clearAndInitProvinces(lazy: () -> List<Address>): List<Address> {
        repo.delete { root, _: CriteriaQuery<*>?, b ->
            b.notEqual(root.get<String>(AnyEntity.ID), repo.findRootId())
        }
        val cleanedRoot = repo.save(repo.findRoot().withNew().apply { id = repo.findRootId() })
        return repo.saveChildren(cleanedRoot, lazy())
    }

    override fun findAllByCodeIn(codes: List<String>): List<Address> {
        return repo.findAllByCodeIn(codes)
    }

    override fun findProvinces(): List<Address> {
        return findDirectChildrenById(repo.findRootId())
    }

    override fun findDirectChildrenByCode(code: String): List<Address> {
        TODO("Not yet implemented")
    }

    override fun findDirectChildrenById(id: String): List<Address> {
        TODO("Not yet implemented")
    }

    override fun findFullPathById(id: String): String {
        TODO("Not yet implemented")
    }

    override fun findFullPathByCode(id: String): String {
        TODO("Not yet implemented")
    }
}
