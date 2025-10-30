package com.github.oursharecar.mongo.group

import com.github.oursharecar.domain.group.Group
import com.github.oursharecar.mongo.repository.MongoCollectionRepository
import com.github.oursharecar.mongo.repository.MongoRepository

class MongoGroupRepository(override val repo: MongoCollectionRepository<MongoGroup>) :
    MongoRepository<Group, MongoGroup>