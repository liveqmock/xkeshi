package com.xpos.common.service;

import java.util.List;

import com.xpos.common.entity.Favorites;
import com.xpos.common.entity.example.FavoritesExample;
import com.xpos.common.utils.Pager;



public interface FavoritesService {

	public List<Favorites> findListByUserId(Pager<Favorites> pager, FavoritesExample favoritesExample, Long id);

	public boolean saveByUser(Favorites favorites);

	public boolean deleteByUser(Favorites favorites);

	public boolean update(Favorites favorites);
	
	public Favorites findOneFavorites(FavoritesExample favoritesExample);
	
	//统计用户的收藏总数
	public Integer countFavorites(FavoritesExample favoritesExample);
	
	public Pager<Favorites>   findListFavorites(Pager<Favorites> pager  , FavoritesExample favoritesExample);
}
