package net.yan100.compose.rds.service

import net.yan100.compose.rds.entities.Address
import net.yan100.compose.rds.service.base.IService

/**
 * # 地址服务
 * @author TrueNine
 * @since 2023-05-08
 */
interface IIAddressService : IService<Address> {
    fun findRoot(): Address
    fun initProvince()
    fun findProvinces(): List<Address>
    fun findDirectChildrenByCode(code: String): List<Address>
    fun findFullPathById(id: String): String
}
