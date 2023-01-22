package joseluisgs.es.services.cache.raquetas

import joseluisgs.es.models.Raqueta
import joseluisgs.es.services.cache.ICache
import java.util.*

interface RaquetasCache : ICache<UUID, Raqueta>