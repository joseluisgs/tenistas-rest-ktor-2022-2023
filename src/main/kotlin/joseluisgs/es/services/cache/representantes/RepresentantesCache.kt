package joseluisgs.es.services.cache.representantes

import joseluisgs.es.models.Representante
import joseluisgs.es.services.cache.ICache
import java.util.*

interface RepresentantesCache : ICache<UUID, Representante>