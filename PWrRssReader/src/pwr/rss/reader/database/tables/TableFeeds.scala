package pwr.rss.reader.database.tables

import android.database.sqlite.SQLiteDatabase
import pwr.rss.reader.database.tables.SQLQueries.DEFAULT_NOT_READ
import pwr.rss.reader.database.tables.SQLQueries.TYPE_BOOLEAN
import pwr.rss.reader.database.tables.SQLQueries.TYPE_DATE
import pwr.rss.reader.database.tables.SQLQueries.TYPE_NUMBER
import pwr.rss.reader.database.tables.SQLQueries.TYPE_STRING
import pwr.rss.reader.database.tables.SQLQueries.C_ID
import pwr.rss.reader.database.tables.SQLQueries.ID
import pwr.rss.reader.data.dao.FeedDao
import pwr.rss.reader.database.dao.Feed
import pwr.rss.reader.R
import java.util.Date

object TableFeeds extends Table {
	val TABLE_NAME_FEEDS = "FEEDS"

	val C_TITLE = "Title"
	val C_IMAGE = "Image"
	val C_LINK = "Link"
	val C_DESCRIPTION = "Description"
	val C_READ = "Read"
	val C_CHANNEL = "Channel"
	val C_ADDED_DATE = "AddedDate"

	private val CREATE_TABLE_FEEDS =
		SQLQueries.CREATE_TABLE + TABLE_NAME_FEEDS +
			"(" +
			C_ID +
			C_TITLE + TYPE_STRING +
			C_IMAGE + TYPE_STRING +
			C_LINK + TYPE_STRING +
			C_DESCRIPTION + TYPE_STRING +
			C_ADDED_DATE + TYPE_DATE +
			C_CHANNEL + TYPE_NUMBER +
			C_READ + TYPE_BOOLEAN + DEFAULT_NOT_READ + ", " +
			"FOREIGN KEY(" + C_CHANNEL + ") REFERENCES " +
			TableChannels.TABLE_NAME_CHANNELS + "(" + ID + ")" +
			");"

	def onCreate(database: SQLiteDatabase) = {
		database.execSQL(CREATE_TABLE_FEEDS)
		// insertInitialFeeds(database)
	}

	/**
	  * ONLY FOR MOCK PURPOSES
	  */
	def insertInitialFeeds(database: SQLiteDatabase) = {
		val feedDao = new FeedDao(database)
		feedDao.addFeed(new Feed(1, "Title 1", "http://www.google.pl/imgres?start=273&client=firefox-a&hs=zO1&rls=org.mozilla:pl:official&channel=fflb&biw=1858&bih=1136&tbs=imgo:1,isz:i&tbm=isch&tbnid=G9D7hzehYmO0lM:&imgrefurl=http://www.iconarchive.com/show/happy-tree-friends-icons-by-sirea/Toothy-icon.html&docid=c00nwArnsCfvTM&imgurl=http://www.iconarchive.com/download/i59641/sirea/happy-tree-friends/Petunie.ico&w=256&h=256&ei=GMv6UamREIaZO4LUgOAL&zoom=1&ved=1t:3588,r:11,s:300,i:37&iact=rc&page=4&tbnh=176&tbnw=176&ndsp=108&tx=85&ty=115", "http://www.link1.pl", "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?", 1, 0L, (new Date(113, 1, 1)).getTime))
		feedDao.addFeed(new Feed(2, "Title 2", "http://www.google.pl/imgres?start=273&client=firefox-a&hs=zO1&rls=org.mozilla:pl:official&channel=fflb&biw=1858&bih=1136&tbs=imgo:1,isz:i&tbm=isch&tbnid=oj1-_6wvKqtbRM:&imgrefurl=http://www.iconarchive.com/show/happy-tree-friends-icons-by-sirea/Petunie-icon.html&docid=c1h47wop5InkFM&imgurl=http://www.iconarchive.com/download/i59637/sirea/happy-tree-friends/Giggles.ico&w=256&h=256&ei=GMv6UamREIaZO4LUgOAL&zoom=1&ved=1t:3588,r:28,s:300,i:88&iact=rc&page=4&tbnh=152&tbnw=152&ndsp=108&tx=65&ty=38", "http://www.link2.pl", "But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but because occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure?", 2, 0L, (new Date(112, 2, 2)).getTime))
		feedDao.addFeed(new Feed(3, "Title 3", "http://www.sireasgallery.com/iconset/happytreefriends/Cub_256x256_32.png", "http://www.link3.pl", "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat.", 2, 0L, (new Date(111, 3, 3)).getTime))
		feedDao.addFeed(new Feed(4, "Title 4", "http://icons.iconarchive.com/icons/sirea/happy-tree-friends/256/Giggles-icon.png", "http://www.link4.pl", "On the other hand, we denounce with righteous indignation and dislike men who are so beguiled and demoralized by the charms of pleasure of the moment, so blinded by desire, that they cannot foresee the pain and trouble that are bound to ensue; and equal blame belongs to those who fail in their duty through weakness of will, which is the same as saying through shrinking from toil and pain. These cases are perfectly simple and easy to distinguish. In a free hour, when our power of choice is untrammelled and when nothing prevents our being able to do what we like best, every pleasure is to be welcomed and every pain avoided. But in certain circumstances and owing to the claims of duty or the obligations of business it will frequently occur that pleasures have to be repudiated and annoyances accepted. The wise man therefore always holds in these matters to this principle of selection: he rejects pleasures to secure other greater pleasures, or else he endures pains to avoid worse pains.", 3, 0L, (new Date(110, 4, 4)).getTime))
		feedDao.addFeed(new Feed(5, "Title 5", "http://icons.iconarchive.com/icons/sirea/happy-tree-friends/256/Cub-icon.png", "http://www.link5.pl", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam vitae ligula ac augue condimentum accumsan. Proin at varius leo. Donec ullamcorper nunc sed nulla ullamcorper, ac rutrum est laoreet. Sed quis nulla fermentum, scelerisque risus et, porta magna. Morbi mattis nulla quam, non ultricies magna euismod a. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent eget placerat turpis. Maecenas ante ligula, aliquet in fermentum ut, pulvinar ut neque. Etiam laoreet, eros at lobortis vestibulum, libero metus hendrerit nisl, at pharetra nisl nisl vel nisl. Nulla non volutpat nunc. ", 3, 0L, (new Date(109, 5, 5)).getTime))
		feedDao.addFeed(new Feed(6, "Title 6", "http://icons.iconarchive.com/icons/sirea/happy-tree-friends/256/TheMole-icon.png", "http://www.link6.pl", "Quisque odio est, luctus et scelerisque a, ullamcorper id turpis. Nullam ut sapien in tellus vehicula rutrum sed et lectus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Pellentesque ultricies lorem diam. Curabitur ut tellus pharetra ipsum sodales pretium ac et nulla. Nullam tempus enim ut ultrices iaculis. Pellentesque accumsan libero non bibendum mollis. Donec porttitor mi iaculis justo fringilla, ut semper risus vestibulum. Aliquam erat volutpat. Suspendisse nec nisl ipsum. Nullam quis pretium leo, sed posuere quam. Etiam et pellentesque mauris. ", 3, 0L, (new Date(108, 6, 6)).getTime))
		feedDao.addFeed(new Feed(7, "Title 7", "http://icons.iconarchive.com/icons/sirea/happy-tree-friends/256/Petunie-icon.png", "http://www.link7.pl", "Sed cursus massa eu tortor placerat fringilla. Quisque condimentum condimentum faucibus. Donec at est commodo libero scelerisque malesuada eu ac urna. Interdum et malesuada fames ac ante ipsum primis in faucibus. Maecenas in turpis commodo, tincidunt tortor sagittis, sodales metus. Proin placerat venenatis urna, sed egestas dolor eleifend sed. Donec mauris velit, dignissim sit amet aliquet nec, dapibus laoreet est. Sed tellus elit, dictum ut nibh nec, tincidunt imperdiet sapien. Suspendisse interdum enim non rhoncus sollicitudin. Nunc dictum elit vitae iaculis auctor. Ut vel diam vel elit scelerisque accumsan. Donec nec nibh facilisis, facilisis ipsum ut, bibendum lacus. Maecenas sed leo a nunc lobortis facilisis sit amet ac risus. Suspendisse blandit arcu eu venenatis scelerisque. ", 4, 0L, (new Date(107, 7, 7)).getTime))
		feedDao.addFeed(new Feed(8, "Title 8", "http://icons.iconarchive.com/icons/sirea/happy-tree-friends/256/Nutty-icon.png", "http://www.link8.pl", "Aliquam urna massa, ullamcorper eu dapibus consequat, varius a ligula. Sed enim risus, ullamcorper ac imperdiet ut, condimentum sit amet nunc. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec non arcu eget diam commodo vehicula nec facilisis nisi. Proin pellentesque libero non odio venenatis sollicitudin. Cras in velit lectus. Nam mattis pulvinar est, nec varius nisl commodo ut. ", 4, 0L, (new Date(106, 8, 8)).getTime))
		feedDao.addFeed(new Feed(9, "Title 9", "http://www.sireasgallery.com/iconset/happytreefriends/Mime_256x256_32.png", "http://www.link9.pl", "Proin auctor elit ut felis adipiscing, at iaculis nibh posuere. Pellentesque malesuada rutrum urna ut sagittis. Vestibulum consequat ultricies lacus, eget porta tellus tincidunt vitae. Ut pellentesque, dolor sed mollis vestibulum, augue orci vehicula nisl, sed fringilla enim nisl quis lectus. Fusce eget consectetur elit. Nullam enim magna, hendrerit et urna in, pharetra tempor velit. Curabitur tristique nisl vitae purus facilisis posuere. Proin non magna ac nisi hendrerit eleifend. Phasellus tristique fringilla sapien, in elementum dolor fermentum at. Cras eu tellus sit amet mauris luctus gravida. Etiam luctus sed nibh et lacinia. Nam risus libero, semper sagittis aliquet at, faucibus eu neque.", 4, 0L, (new Date(105, 9, 9)).getTime))
		feedDao.addFeed(new Feed(10, "Title 10", "http://www.sireasgallery.com/iconset/happytreefriends/Splendid_256x256_32.png", "http://www.link10.pl", "In suscipit lorem dui, ut luctus leo iaculis at. Aenean at bibendum nisi, et suscipit magna. Nam convallis arcu ac elit ultricies imperdiet. Sed adipiscing vestibulum velit et accumsan. Praesent varius, mauris sit amet condimentum placerat, eros eros lacinia arcu, vitae tincidunt mauris ante eu quam. Quisque mi enim, mollis vel facilisis non, semper laoreet lectus. Interdum et malesuada fames ac ante ipsum primis in faucibus. Aliquam interdum rhoncus dignissim. Quisque semper ante eget nulla lobortis facilisis. Nulla leo sapien, tincidunt nec justo at, vulputate bibendum lacus. Pellentesque magna ipsum, vehicula at hendrerit ac, fermentum at dui. Pellentesque id aliquam ante. ", 4, 0L, (new Date(104, 10, 10)).getTime))
	}

	def onUpgrade(database: SQLiteDatabase) = {
		database.execSQL(SQLQueries.DROP_TABLE + TABLE_NAME_FEEDS)
		onCreate(database)
	}
}