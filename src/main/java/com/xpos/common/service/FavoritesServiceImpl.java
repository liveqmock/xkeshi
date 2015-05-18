package com.xpos.common.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xpos.common.entity.Favorites;
import com.xpos.common.entity.example.FavoritesExample;
import com.xpos.common.persistence.mybatis.FavoritesMapper;
import com.xpos.common.persistence.mybatis.UserMapper;
import com.xpos.common.utils.Pager;

@Service
public class FavoritesServiceImpl implements FavoritesService{

	@Resource
	private FavoritesMapper favoritesMapper;

	@Resource
	private UserMapper userMapper;

	@Override
	public List<Favorites> findListByUserId(Pager<Favorites> pager,
			FavoritesExample favoritesExample, Long id) {
			favoritesExample.createCriteria().addCriterion("user_id='"+id+"'");
		return favoritesMapper.selectByExample(favoritesExample, pager);
	}

	@Override
	public boolean saveByUser(Favorites favorites) {
		return favoritesMapper.insert(favorites)==1;
	}

	@Override
	public boolean deleteByUser(Favorites favorites) {
		favorites.setDeleted(true);
		return favoritesMapper.updateByPrimaryKey(favorites)==1;
	}

	@Override
	public boolean update(Favorites favorites) {
	  return favoritesMapper.updateByPrimaryKey(favorites) >0 ;
	}

	@Override
	public Favorites findOneFavorites(FavoritesExample favoritesExample) {
		return favoritesMapper.selectOneByExample(favoritesExample);
	}

	@Override
	public Integer countFavorites(FavoritesExample favoritesExample) {
		return favoritesMapper.countByExample(favoritesExample);
	}

	@Override
	public Pager<Favorites> findListFavorites(Pager<Favorites> pager, FavoritesExample favoritesExample) {
		 if (favoritesExample == null) 
			 favoritesExample =  new FavoritesExample();
		 favoritesExample.createCriteria().addCriterion("deleted = ", false);
		 pager.setList(favoritesMapper.selectByExample(favoritesExample, pager));
		 pager.setTotalCount(favoritesMapper.countByExample(favoritesExample));
		return pager;
	}
	
	
}
