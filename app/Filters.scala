import javax.inject.{Inject, Singleton}

import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter

@Singleton
class Filters @Inject() (corsFilter: CORSFilter) extends DefaultHttpFilters(corsFilter)