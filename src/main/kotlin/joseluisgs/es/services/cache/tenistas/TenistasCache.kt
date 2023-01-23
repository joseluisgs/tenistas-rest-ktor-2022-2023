package joseluisgs.es.services.cache.tenistas

import joseluisgs.es.models.Tenista
import joseluisgs.es.services.cache.ICache
import java.util.*

interface TenistasCache : ICache<UUID, Tenista>